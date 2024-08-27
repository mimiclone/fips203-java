/*
 * Copyright (c) 2024 - Mimiclone, Inc. 
 *

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mimiclone.fips202.keccak.spi;

import com.mimiclone.fips203.hash.XOFParameterSet;
import com.mimiclone.fips202.keccak.core.KeccakSponge;

public final class Shake128StreamCipher extends AbstractSpongeStreamCipher {
	private KeccakSponge sponge;

	@Override
	KeccakSponge getSponge() {
		if(sponge == null) {
			sponge = new KeccakSponge(XOFParameterSet.SHAKE128);
		}
		return sponge;
	}

}
