/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wdqipai.server.handler;

import System.Xml.XmlDocument;
import System.Xml.XmlHelper;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Logger;
import net.wdqipai.core.array.QueueMethod;
import net.wdqipai.core.log.Log;
import net.wdqipai.core.protocol.ClientAction;
import net.wdqipai.core.protocol.RCClientAction;
import net.wdqipai.core.service.IoHandler;
import net.wdqipai.core.socket.AppSession;
import net.wdqipai.core.socket.SessionMessage;
import net.wdqipai.core.util.SHA1ByAdobe;
import net.wdqipai.core.util.SR;
import net.wdqipai.server.RCLogic;
import net.wdqipai.server.RCLogicLPU;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jdom2.Attribute;
import org.jdom2.JDOMException;

/**
 *
 * @author FUX
 */
public class RecordSocketDataHandler implements IoHandler{
    
    
    static final ChannelGroup channels = new DefaultChannelGroup();

    
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e){
        if (e instanceof ChannelStateEvent) {
            System.err.println(e);
        }
        //super.handleUpstream(ctx, e);
    }

    /**
     * channelConnected
     * 
     * @param ctx
     * @param e 
     */
    public void sessionCreated(ChannelHandlerContext ctx, ChannelStateEvent e) {
             
        try
        {
            //request 凭证
            RCLogic.netNeedProof(new AppSession(e.getChannel(),e));
           

        }
        catch (RuntimeException exd)
        {
            Log.WriteStrByException(RecordSocketDataHandler.class.getName(), "channelConnected", exd.getMessage());

        }        
        
    }

    
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        // Unregister the channel from the global channel list
        // so the channel does not receive messages anymore.
        channels.remove(e.getChannel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e,XmlDocument d) {

       
        MessageEvent s = e;        
        
      // Convert to a String first.        
        XmlDocument doc = d;//new XmlDocument();
        //doc.LoadXml((String)message);
        
        //String cAction = doc.DocumentElement.ChildNodes[0].Attributes["action"].Value;

        String csAction = doc.getDocumentElement().getChildren().get(0).getAttribute("action").getValue();
        
        InetSocketAddress remoteAddress = (InetSocketAddress)s.getChannel().getRemoteAddress();
	String strIpPort = remoteAddress.getAddress().getHostAddress() + ":" + String.valueOf(remoteAddress.getPort());
        
        //create item
        SessionMessage item = new SessionMessage(e, doc, false, false);

        //save
        RCLogicLPU.getInstance().getmsgList().Opp(QueueMethod.Add, item);

        //
        Log.WriteStrByRecv(csAction, strIpPort);
        
       
    }
    

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

   

    @Override
    public void sessionOpened() {
       //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sessionClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
