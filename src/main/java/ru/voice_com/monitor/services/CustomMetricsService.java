package ru.voice_com.monitor.services;

import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.transport.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.voice_com.monitor.utils.ReportsProperties;

@Service
@PropertySource("classpath:reports-variables.properties")
public class CustomMetricsService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledReports.class);

    private final SshService sshService;
    private final ReportsProperties reportsProperties;

    public CustomMetricsService(SshService sshService, ReportsProperties reportsProperties) {
        this.sshService = sshService;
        this.reportsProperties = reportsProperties;
    }

    public String getSabrePlLog() {
        String remoteHost = reportsProperties.getPropValue("sabre_pl.remoteHost", String.class);
        String username = reportsProperties.getPropValue("sabre_pl.username", String.class);
        String password = reportsProperties.getPropValue("sabre_pl.password", String.class);
        String command = reportsProperties.getPropValue("sabre_pl.command", String.class);
        try {
            logger.info("Host {} command [{}] preparing to be sent", remoteHost, command);
            return this.sshService.runCommand(remoteHost, username, password, command);
        } catch (ConnectionException e) {
            logger.error("getSabrePlLog error", e);
        } catch (TransportException e) {
            logger.error("getSabrePlLog error", e);
        }
        return "Request sabre_pl ABORTED";
    }
}
