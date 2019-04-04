package org.dog.database.core.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.dog.core.entry.TccLock;
import org.dog.core.util.Pair;
import org.dog.database.core.annotation.DogDb;
import org.dog.database.core.annotation.OperationType;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public abstract class DataLoader implements Iterable<Pair<TccLock,List<Object>>> {



}
