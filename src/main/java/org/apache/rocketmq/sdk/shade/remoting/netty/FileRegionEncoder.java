package org.apache.rocketmq.sdk.shade.remoting.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class FileRegionEncoder extends MessageToByteEncoder<FileRegion> {
    public void encode(ChannelHandlerContext ctx, FileRegion msg, final ByteBuf out) throws Exception {
        WritableByteChannel writableByteChannel = new WritableByteChannel() {
            @Override
            public int write(ByteBuffer src) throws IOException {
                out.writeBytes(src);
                return out.capacity();
            }

            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void close() throws IOException {
            }
        };
        long toTransfer = msg.count();
        while (true) {
            long transferred = msg.transfered();
            if (toTransfer - transferred > 0) {
                msg.transferTo(writableByteChannel, transferred);
            } else {
                return;
            }
        }
    }
}
