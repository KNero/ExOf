package team.balam.exof.listener.handler;

import team.balam.exof.listener.PortInfo;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;

public class LengthFieldByteCodec extends ChannelHandlerArray
{
	protected int maxLength;
	
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
		this.lengthFieldOffset = _info.getAttributeToInt("legthOffset", 0);
		this.lengthFieldLength = _info.getAttributeToInt("lengthSize", 0);
	}
	
	@Override
	public ChannelHandler[] make()
	{
		ChannelHandler[] pipe = new ChannelHandler[]{
				new LengthFieldBasedFrameDecoder(this.maxLength, 
						this.lengthFieldOffset, this.lengthFieldLength, this.lengthAdjustment, this.initialBytesToStrip),
				new ByteArrayDecoder()
		};
		
		return pipe;
	}
}
