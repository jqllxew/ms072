package org.example.ms072.provider.nx;

import org.example.ms072.provider.MapleDataFileEntry;
import org.example.ms072.provider.pkgnx.format.NXNode;

/**
 * @author Aaron
 * @version 1.0
 * @since 6/8/13
 */
public class NXDataFileEntry extends NXDataEntry implements MapleDataFileEntry {

    public NXDataFileEntry(NXNode node, NXData parent) {
        super(node, parent);
    }

    @Override
    public void setOffset(int offset) {
        throw new UnsupportedOperationException("NXDataFileEntry :: implement only if really needed...");
    }
}
