package kconfigdiffer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class KconfigDiffer {

    public static void main(String[] args) {
        
        if (args.length != 3) {
            System.out.println("Usage: <INITIAL_CONFIG> <CONFIG_DIFF> <OUTPUT_CONFIG>.");
            System.exit(1);
        }
        
        String master_config_path = args[0];
        String dev_config_path = args[0];
        String output_config_path = args[0];
        // if one of the files doesnt exist
        if (!(assertFile(master_config_path) && assertFile(dev_config_path))) {
            System.out.println("Error: file is invalid.");
            System.exit(1);
        }
        
        try {
           String master_config = new String(Files.readAllBytes(Paths.get(master_config_path)));
           String dev_config = new String(Files.readAllBytes(Paths.get(dev_config_path)));
           
           String[][] valid_configs = splitConfig(master_config);
           String[][] requested_configs = splitConfig(dev_config);
           
           for (String[] requested_config : requested_configs) {
               String requested_name= requested_config[0];
               String requested_option = requested_config[1];
               
               int config_index = configExists(valid_configs, requested_name);
               
               if (config_index != -1) {
                   valid_configs[config_index][1] = requested_option;
               } else {
                   System.out.printf("Warning: requested config is invalid: %s.\n", requested_name);
               }
               
               String formatted_config = formatConfig(valid_configs);
               
               Path output_config = Paths.get(output_config_path);
               Files.write(output_config, formatted_config.getBytes());
           }
        } catch (IOException e) {
            System.out.println("Error: file is invalid.");
        }
    }
    
    public static boolean assertFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        
        return false;
    }
    
    public static String formatConfig(String[][] all_configs) {
        String formatted_config = "";
        for (int i = 0; i < all_configs.length; i++) {
            String config_name = all_configs[i][0];
            String config_option = all_configs[i][1];
            
            // uncompiled config options do not need to be included
            if (config_option != null && !config_option.equals("n")) {
                formatted_config += config_name + "=" + config_option + "\n";
            }
        }
        
        return formatted_config;
    }
    
    public static int configExists(String[][] all_configs, String search) {
        for (int i = 0; i < all_configs.length; i++) {
            String[] this_config = all_configs[i];
            if (this_config[0] != null && this_config[0].equals(search)) {
                return i;
            }
        }
        
        return -1;
    }
    
    public static String[][] splitConfig(String config) {
        //String[][] all_configs = config.split("\n");
        String[] split_config = config.split("\n");
        String[][] all_configs = new String[split_config.length][2];
        
        for (int i = 0; i < split_config.length; i++) {
            String this_config = split_config[i];
            // it is either `y` or `m`
            if (this_config.contains("=")) {
                // EXAMPLE: CONFIG_NAME=y
                String option = this_config.split("=")[1];
                all_configs[i][0] = this_config.split("=")[0];
                all_configs[i][1] = option;
            // it is not compiled, but it is an option
            } else if (this_config.contains("#") && this_config.contains("CONFIG_")) {
                // EXAMPLE: #CONFIG_NAME is not set
                String config_name = this_config.split(" ")[1];
                config_name = config_name.replace("#", "");
                
                
                all_configs[i][0] = config_name;
                all_configs[i][1] = "n";
            }
        }
        
        return all_configs;
    }
    
}
