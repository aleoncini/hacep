package it.redhat.hacep.client;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Lab {

    private ScheduledExecutorService scheduler = null;

    public Lab() {
        scheduler = Executors.newScheduledThreadPool(getConcurrentPlayers());
    }

    public static void main(String[] args) throws Exception {
        new Lab().experiment();
    }

    private void experiment() throws InterruptedException {
        PooledConnectionFactory connectionFactory = getConnectionFactory();

        for (int i = 0; i < getConcurrentPlayers(); i++) {
            int delay = 1 + i;
            final ScheduledFuture<?> playerHandle = scheduler
                    .scheduleAtFixedRate(
                            new MyRunner(new GameplayProducer(connectionFactory, getQueueName(), i)),
                            delay,
                            getEventInterval(),
                            TimeUnit.SECONDS);
            scheduler.schedule(() -> playerHandle.cancel(true), getDuration(), TimeUnit.MINUTES);
        }
    }

    private PooledConnectionFactory getConnectionFactory() throws InterruptedException {
        ActiveMQConnectionFactory activeMQConnectionFactory;
        if (getSecurity()) {
            activeMQConnectionFactory = new ActiveMQConnectionFactory(getUsername(), getPassword(), "tcp://" + getBrokerHost());
        } else {
            activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://" + getBrokerHost());
        }
        PooledConnectionFactory connectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
        connectionFactory.setMaxConnections(getPoolSize());
        connectionFactory.setMaximumActiveSessionPerConnection(500);
        return connectionFactory;
    }

    private int getPoolSize() {
        try {
            return Integer.valueOf(System.getProperty("pool.size", "8"));
        } catch (IllegalArgumentException e) {
            return 8;
        }
    }

    private int getDuration() {
        try {
            return Integer.valueOf(System.getProperty("duration", "1"));
        } catch (IllegalArgumentException e) {
            return 1;
        }
    }

    private String getBrokerHost() {
        try {
            return System.getProperty("broker.host", "localhost:61616");
        } catch (IllegalArgumentException e) {
            return "localhost:61616";
        }
    }

    private boolean getSecurity() {
        try {
            return Boolean.valueOf(System.getProperty("broker.authentication", "false"));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String getUsername() {
        try {
            return System.getProperty("broker.usr", "");
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    private String getPassword() {
        try {
            return System.getProperty("broker.pwd", "");
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    private String getQueueName() {
        try {
            return System.getProperty("queue.name", "facts");
        } catch (IllegalArgumentException e) {
            return "facts";
        }
    }

    private int getConcurrentPlayers() {
        try {
            return Integer.valueOf(System.getProperty("concurrent.players", "1"));
        } catch (IllegalArgumentException e) {
            return 1;
        }
    }

    private int getEventInterval() {
        try {
            return Integer.valueOf(System.getProperty("event.interval", "10"));
        } catch (IllegalArgumentException e) {
            return 10;
        }
    }

    private class MyRunner implements Runnable {

        private final GameplayProducer gameplayProducer;
        private int id = 10000;

        public MyRunner(GameplayProducer gameplayProducer) {
            this.gameplayProducer = gameplayProducer;
        }

        @Override
        public void run() {
            gameplayProducer.produce(id++, System.currentTimeMillis());
        }
    }
}