package com.jacpower.groupsApp.ruleEngine.interfaces;

public interface IRule <I> {
    boolean matches(I module);
    Object apply(I request);

}
