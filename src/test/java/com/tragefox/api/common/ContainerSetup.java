package com.tragefox.api.common;

import org.testcontainers.mongodb.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public class ContainerSetup extends MongoDBContainer {

    private static final DockerImageName IMAGE = DockerImageName.parse("mongo:7.0");
    private static ContainerSetup container;

    private ContainerSetup() {
        super(IMAGE);
    }

    public static synchronized ContainerSetup getInstance() {
        if (container == null) {
            container = new ContainerSetup();
            container.start();
        }
        return container;
    }

    @Override
    public void stop() {}
}
