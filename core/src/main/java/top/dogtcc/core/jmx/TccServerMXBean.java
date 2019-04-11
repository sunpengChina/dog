package top.dogtcc.core.jmx;

import top.dogtcc.core.entry.DogTcc;

import javax.management.MXBean;
import java.util.List;

@MXBean
public interface TccServerMXBean {

     long getTccNum();

     long getCallNum();

     long getTccErrorNum();

     List<DogTcc>  failsTcc();

}
