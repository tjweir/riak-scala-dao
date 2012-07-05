//
// Copyright 2012, Ray Jenkins
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.aceevo.riak.dao.impl

import com.basho.riak.client.builders.RiakObjectBuilder
import com.basho.riak.client.cap.VClock
import com.basho.riak.client.http.util.Constants
import com.basho.riak.client.IRiakObject
import com.codahale.jerkson.Json._
import com.aceevo.riak.driver.RiakStorageDriver
import com.aceevo.riak.model.PersistentEntity

/**
 * Created with IntelliJ IDEA.
 * User: rjenkins
 * Date: 7/4/12
 * Time: 10:52 PM
 * To change this template use File | Settings | File Templates.
 */

class RiakJSONEntityDAO[K, T <: PersistentEntity](bucket: String,
                                                  storageDriver: RiakStorageDriver[K, T])(implicit mf: Manifest[T])
  extends AbstractRiakEntityDAO[K, T](bucket: String, storageDriver: RiakStorageDriver[K,
    T]) {

  def fromDomain(t: T, vClock: VClock): IRiakObject = {
    val dataAsString = generate(t)
    val data = (dataAsString).map(_.toChar).toCharArray.map(_.toByte)

    RiakObjectBuilder.newBuilder(bucket, t.id).withVClock(vClock)
      .withContentType(Constants.CTYPE_JSON)
      .withValue(data).build()
  }

  def toDomain(riakObject: IRiakObject) = {

    val data = riakObject.getValueAsString
    parse(data)(mf)
  }
}