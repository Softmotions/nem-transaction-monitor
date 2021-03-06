package io.nem.monitor;

import java.util.ArrayList;
import java.util.List;

import org.nem.core.model.NetworkInfos;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import io.nem.model.ChannelHandleModel;
import io.nem.monitor.handler.TransactionMonitorHandler;
import io.nem.utils.DefaultSetting;
import io.nem.utils.ScannerUtil;

public class WsNemTransactionMonitor {

	private WsNemTransactionMonitor() {
	}

	public static INetworkName init() {
		return new WsNemTransactionMonitor.Builder();
	}

	public static INetworkName networkName(String name) {
		return new WsNemTransactionMonitor.Builder(name);
	}

	public interface INetworkName {
		IHost host(String name);
	}

	public interface IHost {
		IPort port(String name);
	}

	public interface IPort {
		IAddress wsPort(String port);
	}

	public interface IAddress {
		IChannel addressesToMonitor(List<String> addresses);
		IChannel addressesToMonitor(String... addresses);
		IChannel addressToMonitor(String address);
	}

	public interface IChannel {
		IChannel subscribe(String channel, TransactionMonitorHandler handler);

		void monitor();
	}

	public static class Builder implements INetworkName, IHost, IPort, IAddress, IChannel {

		private String networkName;
		private String host;
		private String port;
		private String wsPort;
		private String address;
		private List<String> addresses;

		private List<ChannelHandleModel> channelHandleList = new ArrayList<ChannelHandleModel>();

		public Builder() {
		}

		public Builder(String name) {
			this.networkName = name;
			NetworkInfos.setDefault(NetworkInfos.fromFriendlyName(name));
		}

		@Override
		public IChannel subscribe(String channel, TransactionMonitorHandler handler) {

			if (this.addresses != null && !this.addresses.isEmpty()) {
				for(String address:this.addresses) {
					handler.setAddress(address);
					this.channelHandleList.add(new ChannelHandleModel(channel, address, handler));
				}
			} else {
				handler.setAddress(this.address);
				this.channelHandleList.add(new ChannelHandleModel(channel, this.address, handler));
			}
			return this;
		}

		@Override
		public void monitor() {
			DefaultSetting.setHostAndPort(this.host, this.port, this.wsPort);
			if (this.addresses != null && !this.addresses.isEmpty()) {
				for (String address : this.addresses) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							monitor(address);
						}
					}).start();
				}
			} else {
				monitor(this.address);
			}

		}

		private void monitor(String address) {
			final String WS_URI = DefaultSetting.getWsUri();
			// create WebSocket client
			List<Transport> transports = new ArrayList<Transport>(1);
			transports.add(new WebSocketTransport(new StandardWebSocketClient()));
			WebSocketClient transport = new SockJsClient(transports);
			WebSocketStompClient stompClient = new WebSocketStompClient(transport);
			stompClient.setMessageConverter(new StringMessageConverter());
			StompSessionHandler handler = new WsMonitorImcomingSessionHandler(address, this.channelHandleList);
			stompClient.connect(WS_URI, handler);
			// block and monitor exit action
			ScannerUtil.monitorExit();
		}

		@Override
		public IChannel addressToMonitor(String address) {
			this.address = address;
			return this;
		}

		@Override
		public IAddress wsPort(String wsPort) {
			this.wsPort = wsPort;
			return this;
		}

		@Override
		public IPort port(String port) {
			this.port = port;
			return this;
		}

		@Override
		public IHost host(String host) {
			this.host = host;
			return this;
		}

		@Override
		public IChannel addressesToMonitor(List<String> addresses) {
			this.addresses = addresses;
			return this;
		}

		@Override
		public IChannel addressesToMonitor(String... addresses) {
			this.addresses = new ArrayList<String>();
			for (String address : addresses) {
				this.addresses.add(address);
			}
			return this;
		}
	}

}
