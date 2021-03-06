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

package tech.pegasys.artemis.api.schema;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.tuweni.bytes.Bytes32;

public class BeaconBlockBody {
  public final BLSSignature randao_reveal;
  public final Eth1Data eth1_data;
  public final Bytes32 graffiti;
  public final List<ProposerSlashing> proposer_slashings;
  public final List<AttesterSlashing> attester_slashings;
  public final List<Attestation> attestations;
  public final List<Deposit> deposits;
  public final List<SignedVoluntaryExit> voluntary_exits;

  public BeaconBlockBody(tech.pegasys.artemis.datastructures.blocks.BeaconBlockBody body) {
    this.randao_reveal = new BLSSignature(body.getRandao_reveal().toBytes());
    this.eth1_data = new Eth1Data(body.getEth1_data());
    this.graffiti = body.getGraffiti();
    this.proposer_slashings =
        body.getProposer_slashings().stream()
            .map(ProposerSlashing::new)
            .collect(Collectors.toList());
    this.attester_slashings =
        body.getAttester_slashings().stream()
            .map(AttesterSlashing::new)
            .collect(Collectors.toList());
    this.attestations =
        body.getAttestations().stream().map(Attestation::new).collect(Collectors.toList());
    this.deposits = body.getDeposits().stream().map(Deposit::new).collect(Collectors.toList());
    this.voluntary_exits =
        body.getVoluntary_exits().stream()
            .map(SignedVoluntaryExit::new)
            .collect(Collectors.toList());
  }
}
