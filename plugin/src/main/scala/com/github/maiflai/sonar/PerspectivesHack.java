package com.github.maiflai.sonar;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.test.MutableTestPlan;

public class PerspectivesHack {

    private final ResourcePerspectives  perspectives;

    public PerspectivesHack(ResourcePerspectives perspectives) {
        this.perspectives = perspectives;
    }

    public MutableTestPlan testPlan(InputFile inputFile) {
        // the compiler fails to distinguish the correct overloaded method from scala source
        return perspectives.as(MutableTestPlan.class, inputFile);
    }
}