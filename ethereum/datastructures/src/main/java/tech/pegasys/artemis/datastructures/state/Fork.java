/*
 * Copyright 2019 ConsenSys AG.
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

package tech.pegasys.artemis.datastructures.state;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.primitives.UnsignedLong;
import java.util.List;
import java.util.Objects;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.bytes.Bytes32;
import org.apache.tuweni.ssz.SSZ;
import tech.pegasys.artemis.util.SSZTypes.Bytes4;
import tech.pegasys.artemis.util.SSZTypes.SSZContainer;
import tech.pegasys.artemis.util.backing.tree.TreeNode;
import tech.pegasys.artemis.util.backing.type.BasicViewTypes;
import tech.pegasys.artemis.util.backing.type.ContainerViewType;
import tech.pegasys.artemis.util.backing.view.AbstractImmutableContainer;
import tech.pegasys.artemis.util.backing.view.BasicViews.Bytes4View;
import tech.pegasys.artemis.util.backing.view.BasicViews.UInt64View;
import tech.pegasys.artemis.util.hashtree.Merkleizable;
import tech.pegasys.artemis.util.sos.SimpleOffsetSerializable;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class Fork extends AbstractImmutableContainer<Fork>
    implements Merkleizable, SimpleOffsetSerializable, SSZContainer {

  // The number of SimpleSerialize basic types in this SSZ Container/POJO.
  public static final int SSZ_FIELD_COUNT = 3;

  @SuppressWarnings("unused")
  private final Bytes4 previous_version = null; // This is a Version type, aliased as a Bytes4

  @SuppressWarnings("unused")
  private final Bytes4 current_version = null; // This is a Version type, aliased as a Bytes4

  @SuppressWarnings("unused")
  private final UnsignedLong epoch = null;

  public static final ContainerViewType<Fork> TYPE =
      new ContainerViewType<>(
          List.of(
              BasicViewTypes.BYTES4_TYPE, BasicViewTypes.BYTES4_TYPE, BasicViewTypes.UINT64_TYPE),
          Fork::new);

  private Fork(ContainerViewType<Fork> type, TreeNode backingNode) {
    super(type, backingNode);
  }

  public Fork(Bytes4 previous_version, Bytes4 current_version, UnsignedLong epoch) {
    super(
        TYPE,
        new Bytes4View(previous_version),
        new Bytes4View(current_version),
        new UInt64View(epoch));
  }

  public Fork(Fork fork) {
    super(TYPE, fork.getBackingNode());
  }

  @Override
  @JsonIgnore
  public int getSSZFieldCount() {
    return SSZ_FIELD_COUNT;
  }

  @Override
  public List<Bytes> get_fixed_parts() {
    return List.of(
        SSZ.encode(writer -> writer.writeFixedBytes(getPrevious_version().getWrappedBytes())),
        SSZ.encode(writer -> writer.writeFixedBytes(getCurrent_version().getWrappedBytes())),
        SSZ.encodeUInt64(getEpoch().longValue()));
  }

  @Override
  public int hashCode() {
    return hashTreeRoot().slice(0, 4).toInt();
  }

  @Override
  public boolean equals(Object obj) {
    if (Objects.isNull(obj)) {
      return false;
    }

    if (this == obj) {
      return true;
    }

    if (!(obj instanceof Fork)) {
      return false;
    }

    Fork other = (Fork) obj;
    return hashTreeRoot().equals(other.hashTreeRoot());
  }

  /** ******************* * GETTERS & SETTERS * * ******************* */
  @JsonProperty
  public Bytes4 getPrevious_version() {
    return ((Bytes4View) get(0)).get();
  }

  @JsonProperty
  public Bytes4 getCurrent_version() {
    return ((Bytes4View) get(1)).get();
  }

  @JsonProperty
  public UnsignedLong getEpoch() {
    return ((UInt64View) get(2)).get();
  }

  @Override
  public Bytes32 hash_tree_root() {
    return hashTreeRoot();
  }

  @Override
  public String toString() {
    return "Fork{"
        + "previous_version="
        + getPrevious_version()
        + ", current_version="
        + getCurrent_version()
        + ", epoch="
        + getEpoch()
        + '}';
  }
}
