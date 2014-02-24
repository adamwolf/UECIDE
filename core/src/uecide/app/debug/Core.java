package uecide.app.debug;

import java.io.*;
import java.util.*;

import uecide.app.*;

public class Core implements MessageConsumer {
    private String name;
    private File folder;
    public PropertyFile corePreferences;
    private boolean valid;
    private boolean runInVerboseMode;
    private File coreFile;

    public Core(File folder) {
        this.folder = folder;

        coreFile = new File(folder,"core.txt");

        valid = false;

        try {
            if(coreFile.exists()) {
                corePreferences = new PropertyFile(coreFile);
                this.name = folder.getName();
            }
            valid = true;
        } catch (Exception e) {
            System.err.println("Error loading core from " + coreFile + ": " + e);
        }

    }

    public File getCoreFile() {
        return coreFile;
    }

    public void resolveInheritance() {
        String inc = corePreferences.get("inherit");
        if (inc == null || inc.equals("")) {
            return;
        }
        Core c = Base.cores.get(inc);
        corePreferences = new PropertyFile(coreFile, c.getCoreFile());
    }

    public File getLibraryFolder() {
        File lf = new File(folder, get("library.path", "libraries"));
        return lf;
    }

    public String getName() { 
        return name; 
    }

    public File getFolder() { 
        return folder; 
    }

    public boolean isValid() {
        return valid;
    }

    public void message(String m) {
        message(m, 1);
    }

    public void message(String m, int chan) {
        if (m.trim() != "") {
            if (chan == 2) {
                System.err.print(m);
            } else {
                System.out.print(m);
            }
        }
    }

    public String get(String k) {
        if (corePreferences == null) {
            System.err.println("No core data getting " + k);
            return "";
        }
        return (String) corePreferences.get(k);
    }

    public String get(String k, String d) {
        if (get(k) == null) {
            return d;
        }
        return get(k);
    }

    static private boolean createFolder(File folder) {
        if (folder.isDirectory())
            return false;
        if (!folder.mkdir())
            return false;
        return true;
    }

    static public String[] headerListFromIncludePath(String path) {
        FilenameFilter onlyHFiles = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".h");
            }
        };

        return (new File(path)).list(onlyHFiles);
    }

    public PropertyFile getPreferences() {
        return corePreferences;
    }

    public String getRevision() {
        String v = corePreferences.get("revision");
        if (v == null) {
            v = "0";
        }
        return v;
    }

    public String getVersion() {
        String v = corePreferences.get("version");
        if (v == null) {
            v = "0";
        }
        return v;
    }

    public String getFullVersion() {
        return getVersion() + "-" + getRevision();
    }

    public String getFamily() {
        return corePreferences.get("family");
    }

    public boolean inFamily(String fam) {
        String fly = getFamily();
        if (fly == null) {
            return false;
        }
        String fams[] = fly.split("::");
        for (String thisfam : fams) {
            if (thisfam.equals(fam)) {
                return true;
            }
        }
        return false;
    }

    public PropertyFile getProperties() {
        return corePreferences;
    }

    public Compiler getCompiler() {
        return Base.compilers.get(corePreferences.get("compiler"));
    }

    public File getExamplesFolder() {
        String f = corePreferences.get("examples.path");
        if (f == null || f == "") {
            f = "examples";
        }
        return new File(folder, f);
    }

    public File getManual() {
        String m = corePreferences.get("manual");
        if (m == null) {
            return null;
        }
        File mf = new File(folder, m);
        if (!mf.exists()) {
            return null;
        }
        return mf;
    }

}
