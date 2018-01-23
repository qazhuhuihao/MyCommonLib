package cn.hhh.commonlib.imp;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author qazhu
 * @date 2018/1/23
 */

public class FileNameSelector implements FilenameFilter {
    private String[] extensions;

    public FileNameSelector(String... fileExtensionNoDots) {
        extensions = fileExtensionNoDots;
    }

    @Override
    public boolean accept(File dir, String name) {
        for (String extension : extensions) {
            if (name.endsWith(extension))
                return true;
        }

        return false;
    }
}
