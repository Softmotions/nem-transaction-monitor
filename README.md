# NEM Transaction Monitor

Java library for monitoring transactions in NEM Blockchain platform

<h2>How to build</h2>

```bash
git clone https://github.com/NEMPH/nem-transaction-monitor.git
cd nem-transaction-monitor
mvn clean install
```

Import it as a maven dependency

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>nem-transaction-monitor</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

<h2>Usage</h2>

Simply add the following code on your Java Application.

```java

WsNemTransactionMonitor.networkName("<network name>").host("<node url>").port("7895").wsPort("7778")
	.addressToMonitor("MDYSYWVWGC6JDD7BGE4JBZMUEM5KXDZ7J77U4X2Y") // address to monitor
	.subscribe(io.nem.utils.Constants.URL_WS_TRANSACTIONS, new TransactionMonitor()) // multiple subscription and a handler
	.subscribe(io.nem.utils.Constants.URL_WS_UNCONFIRMED, new UnconfirmedTransactionMonitor())
	.monitor(); // trigger the monitoring process
			
```

You can also monitor multiple addresses

```java

WsNemTransactionMonitor.networkName("<network name>").host("<node url>").port("7895").wsPort("7778")
	.addressesToMonitor("MDYSYWVWGC6JDD7BGE4JBZMUEM5KXDZ7J77U4X2Y","MDYSYWVWGC6JDD7BGE4JBZMUED7BGE4JBD") // address to monitor
	.subscribe(io.nem.utils.Constants.URL_WS_TRANSACTIONS, new TransactionMonitor()) // multiple subscription and a handler
	.subscribe(io.nem.utils.Constants.URL_WS_UNCONFIRMED, new UnconfirmedTransactionMonitor())
	.monitor(); // trigger the monitoring process
			
```

<h3>Custom Transaction Monitor</h3>
You can create your own handler to handle the incoming payload. 

```java
public class CustomTransactionMonitor implements TransactionMonitorHandler {
	@Override
	public Type getPayloadType(StompHeaders headers) {
		return String.class;
	}
	@Override
	public void handleFrame(StompHeaders headers, Object payload) {
		System.out.println(payload.toString()); // handle the payload.
	}
}
```

```java
WsNemTransactionMonitor.networkName("<network name>").host("<node url>").port("7895").wsPort("7778")
	.addressToMonitor("MDYSYWVWGC6JDD7BGE4JBZMUEM5KXDZ7J77U4X2Y")
	.subscribe(io.nem.utils.Constants.URL_WS_TRANSACTIONS, new CustomTransactionMonitor())
	.monitor();
```

<h2>Support</h2>
Need help integration your Java Application with NEM.io Platform? I can definitely help you with that, send me a message via 

[telegram](https://web.telegram.org/#/im?p=@brambear)


Tips are appreciated but not required. :bowtie: :muscle: :metal:  
XEM: NA6IT2-ZSTQLT-YO223Z-ZMH2J7-2GVG7G-ZY72FN-47IF  
BTC: 3JYAYPxN9RL4UvbxMd1svbQynMpFbf5ehy 

<sub>Copyright (c) 2017</sub>
