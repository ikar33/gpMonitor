package ru.voice_com.monitor.services;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SshService {

    private static final Logger logger = LoggerFactory.getLogger(SshService.class);

    private static Map<String, SSHClient> clientsBooster = new HashMap<>();

    public String runCommand(String host, String username, String password, String command) throws ConnectionException, TransportException {
        String result = "";
        Session session = this.getSession(host, username, password);
        Session.Command cmd = null;
        if (session != null) {
            try {
                session.allocateDefaultPTY();
                cmd = session.exec(command);
                cmd.join(30, TimeUnit.SECONDS);
                return IOUtils.readFully(cmd.getInputStream()).toString();
            } catch (ConnectionException e) {
                logger.error("Command [" + command + "] to ssh host " + host + " aborted.", e);
            } catch (TransportException e) {
                logger.error("Command [" + command + "] to ssh host " + host + " aborted.", e);
            } catch (IOException e) {
                logger.error("Command [" + command + "] to ssh host " + host + " aborted.", e);
            } finally {
                if (cmd != null)
                    cmd.close();
                if(session != null)
                    session.close();
            }
        }
        return result;
    }

    private Session getSession(String host, String username, String password) throws ConnectionException, TransportException {
        SSHClient client = null;
        Session session = null;
        if (clientsBooster.containsKey(host)) {
            client = clientsBooster.get(host);
        }
        if (client == null || !client.isAuthenticated()) {
            client = this.authorize(host, username, password);
            if (client != null && client.isAuthenticated()) {
                clientsBooster.put(host, client);
                session = client.startSession();
            }
        }else{
            session = client.startSession();
        }
        return session;
    }

    private SSHClient authorize(String host, String username, String password) {
        SSHClient client = new SSHClient();
        if (client != null) {
            try {
                client.addHostKeyVerifier(new PromiscuousVerifier());
                client.connect(host);
                client.authPassword(username, password);
                return client;
            } catch (IOException e) {
                logger.error("SSH Connection to host " + host + " aborted.", e);
            }
        }
        return null;
    }
}
