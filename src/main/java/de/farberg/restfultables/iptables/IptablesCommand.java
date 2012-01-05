/**
 * Copyright (c) 2012, Dennis Pfisterer, Institute of Telematics, University of Luebeck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 	- Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * 	  disclaimer.
 * 	- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * 	  following disclaimer in the documentation and/or other materials provided with the distribution.
 * 	- Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or promote
 * 	  products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.farberg.restfultables.iptables;

import java.util.HashMap;
import java.util.Map;

public enum IptablesCommand {
    list, listrules, append, delete, insert, replace, flush, zero, newchain, deletechain, policy, renamechain, help, unknown;

    private static final Map<IptablesCommand, String> m = new HashMap<>();

    static {
        m.put(list, "list");
        m.put(listrules, "list-rules");
        m.put(append, "append");
        m.put(delete, "delete");
        m.put(insert, "insert");
        m.put(replace, "replace");
        m.put(flush, "flush");
        m.put(zero, "zero");
        m.put(newchain, "new-chain");
        m.put(deletechain, "delete-chain");
        m.put(policy, "policy");
        m.put(renamechain, "rename-chain");
        m.put(help, "help");
    }

    public static IptablesCommand fromIptablesCommandString(String iptablesCommand) {
        for (IptablesCommand c : m.keySet())
            if (m.get(c).equals(iptablesCommand))
                return c;

        return unknown;
    }

    public String toIpTablesCommandString() {
        return m.get(this);
    }
}
