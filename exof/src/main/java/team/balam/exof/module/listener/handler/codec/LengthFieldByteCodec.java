package team.balam.exof.module.listener.handler.codec;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;

public class LengthFieldByteCodec implements ChannelHandlerArray {
	private int maxLength;
	
	protected int lengthFieldOffset;
	protected int lengthFieldLength;
	protected int lengthAdjustment;
	protected int initialBytesToStrip;
	
	public LengthFieldByteCodec() {
		this.lengthFieldOffset = 0;
		this.lengthFieldLength = 4;
		this.lengthAdjustment = 0;
		this.initialBytesToStrip = 0;
	}
	
	public LengthFieldByteCodec(int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		this.lengthFieldOffset = lengthFieldOffset;
		this.lengthFieldLength = lengthFieldLength;
		this.lengthAdjustment = lengthAdjustment;
		this.initialBytesToStrip = initialBytesToStrip;
	}
	
	@Override
	public void init(PortInfo info) {
		this.maxLength = info.getAttributeToInt(EnvKey.Listener.MAX_LENGTH, 1024 * 8);
		this.lengthFieldOffset = info.getAttributeToInt(EnvKey.Listener.LENGTH_OFFSET, 0);
		this.lengthFieldLength = info.getAttributeToInt(EnvKey.Listener.LENGTH_SIZE, 0);
	}
	
	@Override
	public ChannelHandler[] make(SocketChannel socketChannel) {
		return new ChannelHandler[]{
				new LengthFieldBasedFrameDecoder(this.maxLength, 
						this.lengthFieldOffset, this.lengthFieldLength, this.lengthAdjustment, this.initialBytesToStrip),
				new ByteArrayDecoder()
		};
	}
}
