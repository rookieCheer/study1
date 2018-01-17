package update;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
  
/**  
 * @author obullxl  
 *  
 * email: obullxl@163.com  MSN: obullxl@hotmail.com  QQ: 303630027  
 *  
 * Blog: http://obullxl.iteye.com  
 */  
public final class ClassLoaderUtil {  
	
	private static Logger log = Logger.getLogger(ClassLoaderUtil.class);
    private static Method addURL = initAddMethod();   
       
    private static final Method initAddMethod() {   
        try {   
            Method add = URLClassLoader.class  
                .getDeclaredMethod("addURL", new Class[] { URL.class });   
            add.setAccessible(true);   
            return add;   
        } catch (Exception e) {   
        	log.error("initAddMethod",e);   
        }   
        return null;   
    }   
  
    private static URLClassLoader system = (URLClassLoader) ClassLoader.getSystemClassLoader();   
  
    private static final void loopFiles(File file, List<File> files) {   
        if (file.isDirectory()) {   
            File[] tmps = file.listFiles();   
            for (File tmp : tmps) {   
                loopFiles(tmp, files);   
            }   
        } else {   
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {   
                files.add(file);   
            }   
        }   
    }   
  
    public static final void loadJarFile(File file) {   
        try {   
            addURL.invoke(system, new Object[] { file.toURI().toURL() });   
        } catch (Exception e) {   
        	log.error("loadJarFile",e);   
        }   
    }   
  
    public static final void loadJarPath(String path) {   
        List<File> files = new ArrayList<File>();   
        File lib = new File(path);
        loopFiles(lib, files);   
        for (File file : files) {   
            loadJarFile(file);   
        }   
    }   
}  
