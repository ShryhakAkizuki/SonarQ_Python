# Test para InsecureYamlLoad - SEGURO

import yaml

def cargar_config_segura(yaml_file):
    # SEGURO: yaml.safe_load() solo tipos básicos
    with open(yaml_file) as f:
        config = yaml.safe_load(f)
    
    # SEGURO: Especificar SafeLoader explícitamente
    with open(yaml_file) as f:
        config_alt = yaml.load(f, Loader=yaml.SafeLoader)
    
    return config, config_alt

if __name__ == "__main__":
    print("Carga YAML segura")

# Made with Bob
