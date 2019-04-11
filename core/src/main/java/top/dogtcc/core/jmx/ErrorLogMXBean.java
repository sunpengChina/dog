package top.dogtcc.core.jmx;

import top.dogtcc.core.common.Pair;
import top.dogtcc.core.entry.DogCall;
import top.dogtcc.core.entry.DogTcc;
import top.dogtcc.core.entry.TccContext;

import javax.management.MXBean;
import java.util.List;

@MXBean
public interface ErrorLogMXBean {

    List<Error> fails();


}
