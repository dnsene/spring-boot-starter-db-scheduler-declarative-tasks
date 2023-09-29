package io.github.dnsene.dbscheduler.declarativetasks.boot.bfpp;

import io.github.dnsene.dbscheduler.declarativetasks.boot.startup.StartupException;
import com.github.kagkarlsson.scheduler.task.Task;
import io.github.dnsene.dbscheduler.declarativetasks.boot.util.Pair;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.*;
import java.util.stream.Collectors;


public class TaskBeanRegistrarBeanFactoryPostProcessor implements BeanFactoryPostProcessor {


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        List<TaskDeclaration> taskDeclarations = new ArrayList<>();
        ComponentAnnotatedBeanTaskFinder componentAnnotatedBeanTaskFinder = new ComponentAnnotatedBeanTaskFinder();
        BeanAnnotatedBeanTaskFinder beanAnnotatedBeanTaskFinder = new BeanAnnotatedBeanTaskFinder();

        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames){
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            if(componentAnnotatedBeanTaskFinder.isComponentAnnotatedAndTaskBeanDefinition(beanDefinition)){
                List<TaskDeclaration> beanDeclarations = componentAnnotatedBeanTaskFinder.getTaskDeclarations(beanDefinitionName, beanDefinition);
                taskDeclarations.addAll(beanDeclarations);
            }
            if(beanAnnotatedBeanTaskFinder.isBeanAnnotatedAndTaskBeanDefinition(beanDefinition)){
                List<TaskDeclaration> beanDeclarations = beanAnnotatedBeanTaskFinder.getTaskDeclarations(beanDefinitionName, beanDefinition);
                taskDeclarations.addAll(beanDeclarations);
            }
        }

        assertNoDuplicateTaskDeclaration(taskDeclarations);

        TaskBeanDefinitionsBuilder taskBeanDefinitionsBuilder = new TaskBeanDefinitionsBuilder(beanFactory);
        List<Pair<String, Task<?>>> beanDefinitions = taskDeclarations
                .stream()
                .map(taskBeanDefinitionsBuilder::createBeans)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        beanDefinitions.forEach( pair -> beanFactory.registerSingleton(pair.getFirst(), pair.getSecond()) );
    }

    private void assertNoDuplicateTaskDeclaration(List<TaskDeclaration> taskDeclarations) {
        List<String> duplicateTaskNames = taskDeclarations
                .stream()
                .collect(Collectors.groupingBy(TaskDeclaration::getTaskName, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey).collect(Collectors.toList());
        if (!duplicateTaskNames.isEmpty()) {
            throw new StartupException(
                    "Following Task names are duplicated : " + Arrays.toString(duplicateTaskNames.toArray()),
                    "Modify some Task names");
        }


    }
}
