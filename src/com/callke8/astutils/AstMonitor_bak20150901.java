package com.callke8.astutils;

import java.io.IOException;

import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.asteriskjava.fastagi.DefaultAgiServer;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.event.BridgeEvent;
import org.asteriskjava.manager.event.ManagerEvent;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.callke8.utils.BlankUtils;

@SuppressWarnings("serial")
public class AstMonitor_bak20150901 extends HttpServlet implements ManagerEventListener,Job {

	private static ManagerConnectionFactory factory;
	private static ManagerConnection conn;
	private static Log log = LogFactory.getLog(AstMonitor.class);
	private static String astHost;
	private static int astPort;
	private static String astUser;
	private static String astPass;
	private static int i = 0;
	private static String state;  //连接状态
	private static DefaultAgiServer agiServer;

	public AstMonitor_bak20150901() {
		System.out.println("执行AstMonitor()构造方法...");
		
		if(BlankUtils.isBlank(factory)) {
			factory = new ManagerConnectionFactory(astHost, astPort, astUser,astPass);
			conn = factory.createManagerConnection();
			conn.addEventListener(this);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if(i%10==0) {
			i=0;
		}
		i += 1;
		
		state = BlankUtils.isBlank(conn)?null:conn.getState().toString();
		
		log.info("连接状态为---" + i + ":" + state);
		if(state == null || !state.equalsIgnoreCase("CONNECTED")) {
			try {
				if(state.equalsIgnoreCase("RECONNECTING")) {   //如果状态为 RECONNECTING 时，需要先 logoff ，然后再重新连接
					conn.logoff();
				};
				conn.login();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (AuthenticationFailedException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void onManagerEvent(ManagerEvent event) {
		
		//log.info(event);
		if(event instanceof BridgeEvent) {
			log.info("================================");
			BridgeEvent bridgeEvent = (BridgeEvent)event;
			log.info(bridgeEvent);
			
		}
		
	}

	/*public static String getAstHost() {
		return astHost;
	}

	public static void setAstHost(String astHost) {
		AstMonitor.astHost = astHost;
	}

	public static int getAstPort() {
		return astPort;
	}

	public static void setAstPort(int astPort) {
		AstMonitor.astPort = astPort;
	}

	public static String getAstUser() {
		return astUser;
	}

	public static void setAstUser(String astUser) {
		AstMonitor.astUser = astUser;
	}

	public static String getAstPass() {
		return astPass;
	}

	public static void setAstPass(String astPass) {
		AstMonitor.astPass = astPass;
	}
	*/
	public static ManagerConnectionFactory getManagerConnectionFactory() {
		return factory;
	}
}
