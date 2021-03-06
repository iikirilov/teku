/*
 * Copyright 2020 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.artemis.beaconrestapi.handlers.validator;

import static com.google.common.primitives.UnsignedLong.ONE;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tech.pegasys.artemis.beaconrestapi.RestApiConstants.RANDAO_REVEAL;

import io.javalin.http.Context;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import tech.pegasys.artemis.api.ValidatorDataProvider;
import tech.pegasys.artemis.api.schema.BLSSignature;
import tech.pegasys.artemis.beaconrestapi.RestApiConstants;
import tech.pegasys.artemis.beaconrestapi.schema.BadRequest;
import tech.pegasys.artemis.provider.JsonProvider;
import tech.pegasys.artemis.storage.ChainDataUnavailableException;
import tech.pegasys.artemis.util.async.SafeFuture;

public class GetNewBlockTest {

  private final tech.pegasys.artemis.util.bls.BLSSignature signatureInternal =
      tech.pegasys.artemis.util.bls.BLSSignature.random(1234);
  private BLSSignature signature = new BLSSignature(signatureInternal);
  private Context context = mock(Context.class);
  private final ValidatorDataProvider provider = mock(ValidatorDataProvider.class);
  private final JsonProvider jsonProvider = new JsonProvider();
  private GetNewBlock handler;

  @BeforeEach
  public void setup() {
    handler = new GetNewBlock(provider, jsonProvider);
  }

  @Test
  void shouldPropagateChainDataUnavailableExceptionToGlobalExceptionHandler() throws Exception {
    final Map<String, List<String>> params =
        Map.of(
            RestApiConstants.SLOT, List.of("1"), RANDAO_REVEAL, List.of(signature.toHexString()));
    when(context.queryParamMap()).thenReturn(params);
    when(provider.getUnsignedBeaconBlockAtSlot(ONE, signature))
        .thenReturn(SafeFuture.failedFuture(new ChainDataUnavailableException()));
    handler.handle(context);

    // Exeception should just be propagated up via the future
    verify(context, never()).status(anyInt());
    verify(context)
        .result(
            argThat((ArgumentMatcher<SafeFuture<?>>) CompletableFuture::isCompletedExceptionally));
  }

  @Test
  void shouldRequireThatRandaoRevealIsSet() throws Exception {
    badRequestParamsTest(Map.of(), "'randao_reveal' cannot be null or empty.");
  }

  @Test
  void shouldRequireThatSlotIsSet() throws Exception {
    badRequestParamsTest(
        Map.of(RANDAO_REVEAL, List.of(signature.toHexString())), "'slot' cannot be null or empty.");
  }

  @Test
  void shouldReturnServerErrorWhenRuntimeExceptionReceived() throws Exception {
    final Map<String, List<String>> params =
        Map.of(
            RestApiConstants.SLOT, List.of("1"), RANDAO_REVEAL, List.of(signature.toHexString()));
    when(context.queryParamMap()).thenReturn(params);
    when(provider.getUnsignedBeaconBlockAtSlot(ONE, signature))
        .thenReturn(SafeFuture.failedFuture(new RuntimeException("TEST")));
    handler.handle(context);

    // Exeception should just be propagated up via the future
    verify(context, never()).status(anyInt());
    verify(context)
        .result(
            argThat((ArgumentMatcher<SafeFuture<?>>) CompletableFuture::isCompletedExceptionally));
  }

  @Test
  void shouldReturnBadRequestErrorWhenIllegalArgumentExceptionReceived() throws Exception {
    final Map<String, List<String>> params =
        Map.of(
            RestApiConstants.SLOT, List.of("1"), RANDAO_REVEAL, List.of(signature.toHexString()));
    when(context.queryParamMap()).thenReturn(params);
    when(provider.getUnsignedBeaconBlockAtSlot(ONE, signature))
        .thenReturn(SafeFuture.failedFuture(new IllegalArgumentException("TEST")));
    handler.handle(context);

    verify(context).status(SC_BAD_REQUEST);
  }

  private void badRequestParamsTest(final Map<String, List<String>> params, String message)
      throws Exception {
    when(context.queryParamMap()).thenReturn(params);

    handler.handle(context);
    verify(context).status(SC_BAD_REQUEST);

    if (StringUtils.isNotEmpty(message)) {
      BadRequest badRequest = new BadRequest(message);
      verify(context).result(jsonProvider.objectToJSON(badRequest));
    }
  }
}
