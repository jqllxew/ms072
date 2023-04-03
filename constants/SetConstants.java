package constants;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetConstants {

    private static SetConstants instance = null;
    private static boolean CANLOG;
    private static final Logger log = LoggerFactory.getLogger(SetConstants.class);

    public static SetConstants getInstance() {
        if (instance == null) {
            instance = new SetConstants();
        }
        return instance;
    }
    private Properties itempb_cfg;
    private String itempb_id[];
    private String itemjy_id[];
    private String itemgy_id[];
    private String mappb_id[];
    private String skill_id[];

    public SetConstants() {
        itempb_cfg = new Properties();
        try {
            InputStreamReader is = new FileReader("ms072server.properties");
            itempb_cfg.load(is);
            is.close();
            itempb_id = itempb_cfg.getProperty("cashban").split(",");
            itemjy_id = itempb_cfg.getProperty("jzjy", "0").split(",");
            itemgy_id = itempb_cfg.getProperty("gysj", "0").split(",");
            skill_id = itempb_cfg.getProperty("skillid").split(",");
        } catch (IOException e) {
            log.error("Could not ms072server.properties", e);
        }
    }
    
    public String[] getSkillfj_id() {
        return skill_id;
    }
    
    public String[] getItempb_id() {
        return itempb_id;
    }

    public String[] getItemgy_id() {
        return itemgy_id;
    }

    public String[] getItemjy_id() {
        return itemjy_id;
    }

    public String[] getMappb_id() {
        return mappb_id;
    }

    public boolean isCANLOG() {
        return CANLOG;
    }

    public void setCANLOG(boolean CANLOG) {
        SetConstants.CANLOG = CANLOG;
    }

}
