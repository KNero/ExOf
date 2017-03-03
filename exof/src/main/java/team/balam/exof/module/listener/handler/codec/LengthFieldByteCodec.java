package team.balam.exof.module.listener.handler.codec;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import team.balam.exof.environment.EnvKey;
import team.balam.exof.module.listener.PortInfo;
import team.balam.exof.module.listener.handler.ChannelHandlerArray;

public class LengthFieldByteCodec extends ChannelHandlerArray
{
	private int maxLength;
	
	protected int lengthFieldOffset;
	protected int lengthFieldLength;
	protected int lengthAdjustment;
	protected int initialBytesToStrip;
	
	public LengthFieldByteCodec()
	{
		this.lengthFieldOffset = 0;
		this.lengthFieldLength = 4;
		this.lengthAdjustment = 0;
		this.initialBytesToStrip = 0;
	}
	
	public LengthFieldByteCodec(int _lengthFieldOffset, int _lengthFieldLength, int _lengthAdjustment, int _initialBytesToStrip)
	{
		this.lengthFieldOffset = _lengthFieldOffset;
		this.lengthFieldLength = _lengthFieldLength;
		this.lengthAdjustment = _lengthAdjustment;
		this.initialBytesToStrip = _initialBytesToStrip;
	}
	
	@Override
	public void init(PortInfo _info)
	{
		this.maxLength = _info.getAttributeToInt(EnvKey.Listener.MAX_LENGTH, 1024 * 8);
		this.lengthFieldOffset = _info.getAttributeToInt(EnvKey.Listener.LENGTH_OFFSET, 0);
		this.lengthFieldLength = _info.getAttributeToInt(EnvKey.Listener.LENGTH_SIZE, 0);
	}
	
	@Override
	public void destroy() 
	{
		
	}
	
	@Override
	public ChannelHandler[] make(SocketChannel _socketChannel)
	{
		ChannelHandler[] pipe = new ChannelHandler[]{
				new LengthFieldBasedFrameDecoder(this.maxLength, 
						this.lengthFieldOffset, this.lengthFieldLength, this.lengthAdjustment, this.initialBytesToStrip),
				new ByteArrayDecoder()
		};
		
		return pipe;
	}
}
