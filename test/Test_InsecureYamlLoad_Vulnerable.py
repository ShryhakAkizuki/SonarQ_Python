# Test para InsecureYamlLoad - VULNERABLE

import yaml

def cargar_config_insegura(yaml_file):
    # HIGH: yaml.load() sin SafeLoader
    with open(yaml_file) as f:
        config1 = yaml.load(f)
    
    # HIGH: yaml.full_load() permite objetos Python
    with open(yaml_file) as f:
        config2 = yaml.full_load(f)
    
    # HIGH: yaml.unsafe_load() completamente inseguro
    with open(yaml_file) as f:
        config3 = yaml.unsafe_load(f)
    
    return config1, config2, config3

if __name__ == "__main__":
    print("Carga YAML insegura")

# Made with Bob
