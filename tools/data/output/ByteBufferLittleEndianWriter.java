package tools.data.output;/*package tools.data.output;

import org.apache.mina.common.ByteBuffer;

public class ByteBufferLittleEndianWriter extends GenericLittleEndianWriter {

    private ByteBuffer bb;

    public ByteBufferLittleEndianWriter() {
        this(50, true);
    }

    public ByteBufferLittleEndianWriter(final int size) {
        this(size, false);
    }

    public ByteBufferLittleEndianWriter(final int initialSize, final boolean autoExpand) {
        bb = ByteBuffer.allocate(initialSize);
        bb.setAutoExpand(autoExpand);
        setByteOutputStream(new ByteBufferOutputstream(bb));
    }

    public ByteBuffer getFlippedBB() {
        return bb.flip();
    }

    public ByteBuffer getByteBuffer() {
        return bb;
    }
}
*/