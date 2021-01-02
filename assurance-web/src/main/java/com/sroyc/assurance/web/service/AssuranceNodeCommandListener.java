package com.sroyc.assurance.web.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.sroyc.noderegistrar.exception.CommandExecutionFailuer;
import com.sroyc.noderegistrar.main.NodeCommandListener.CommandListener;

@Component
public class AssuranceNodeCommandListener implements CommandListener, ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOGGER = LogManager.getLogger(AssuranceNodeCommandListener.class);
	private final Map<String, AssuranceCommandExecutor> commandExecutors = new ConcurrentHashMap<>();

	private ApplicationContext context;

	@Autowired
	public AssuranceNodeCommandListener(ApplicationContext context) {
		super();
		this.context = context;
	}

	@Override
	public void listen(String command) throws CommandExecutionFailuer {
		AssuranceCommandExecutor executor = this.commandExecutors.get(command);
		if (executor != null) {
			executor.execute();
		} else {
			throw new CommandExecutionFailuer("Unknown command [" + command + "]");
		}
	}

	public void addListener(String command, AssuranceCommandExecutor executor) {
		this.commandExecutors.put(command, executor);
	}

	public void removeListener(String command) {
		this.commandExecutors.remove(command);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Map<String, AssuranceCommandExecutor> beans = this.context.getBeansOfType(AssuranceCommandExecutor.class);
		if (!CollectionUtils.isEmpty(beans)) {
			beans.values().forEach(bean -> {
				this.addListener(bean.command(), bean);
				LOGGER.info("Listener [{}] has been registered for command [{}]", bean.getClass().getCanonicalName(),
						bean.command());
			});
		}
	}

	public static interface AssuranceCommandExecutor {
		void execute() throws CommandExecutionFailuer;

		String command();
	}

}
