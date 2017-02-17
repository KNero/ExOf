package team.balam.exof.module.group;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupPortListener extends Thread implements Closeable
{
	private static final Logger logger = LoggerFactory.getLogger(GroupPortListener.class);
	
	private volatile boolean isRun;
	private int port;
	
	private ServerSocketChannel serverSocket;
	private Selector selector;
	
	@Override
	public void start() 
	{
//		this.serverSocket = ServerSocketChannel.open();
//		this.serverSocket.configureBlocking(false);
//		this.serverSocket.bind(new InetSocketAddress(this.port));
//
//		this.selector = Selector.open();
//		this.serverSocket.register(this.selector, SelectionKey.OP_ACCEPT);
//
//		new Thread(() -> {
//			while(this.isRun)
//			{
//				try
//				{
//					int selectCount = this.selector.select(1);
//					
//					if(selectCount > 0)
//					{
//						for(SelectionKey key : this.selector.selectedKeys())
//						{
//							if(key.isAcceptable())
//							{
//								SocketChannel clientChannel = ((ServerSocketChannel)key.channel()).accept();
//								
//								if(clientChannel != null)
//								{
//									clientChannel.configureBlocking(false);
//									clientChannel.register(this.selector, SelectionKey.OP_READ);
//									
//									System.out.println("Client is connected. " + clientChannel);
//								}
//							}
//							else if(key.isReadable())
//							{
//								SocketChannel clientChannel = (SocketChannel)key.channel();
//								
//								ByteArrayOutputStream out = new ByteArrayOutputStream();
//								ByteBuffer buf = ByteBuffer.allocate(1024);
//								int read = 0;
//								while((read = clientChannel.read(buf)) != -1)
//								{
//									out.write(buf.array(), 0, read);
//								}
//								
//								System.out.println(out.toString());
//							}
//						}
//					}
//				}
//				catch(IOException e)
//				{
//					e.printStackTrace();
//				}
//			}
//			
//			try 
//			{
//				this.serverSocket.close();
//				this.selector.close();
//			}
//			catch(IOException e) 
//			{
//				e.printStackTrace();
//			}
//		}).start();
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
