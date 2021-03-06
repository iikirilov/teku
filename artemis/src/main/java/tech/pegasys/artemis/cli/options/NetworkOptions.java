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

package tech.pegasys.artemis.cli.options;

import picocli.CommandLine;

public class NetworkOptions {

  public static final String NETWORK_OPTION_NAME = "--network";

  public static final String DEFAULT_NETWORK = "minimal";

  @CommandLine.Option(
      names = {"-n", NETWORK_OPTION_NAME},
      paramLabel = "<NETWORK>",
      description = "Represents which network to use",
      arity = "1")
  private String network = DEFAULT_NETWORK;

  public String getNetwork() {
    return network;
  }
}
