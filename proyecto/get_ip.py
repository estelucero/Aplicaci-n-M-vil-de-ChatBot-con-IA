import platform
import subprocess

def get_local_ip():
    try:
        system = platform.system()
        if system == "Linux":
            result = subprocess.check_output(["ifconfig", "wlo1"]).decode("utf-8")
            ip_line = [line for line in result.split("\n") if "inet " in line][0]
            ip = ip_line.split()[1]
            return ip
        elif system == "Windows":
            result = subprocess.check_output(["ipconfig"]).decode("utf-8")
            ip_line = [line for line in result.split("\n") if "IPv4 Address" in line][0]
            ip = ip_line.split(":")[1].strip()
            return ip
        else:
            return "Sistema operativo no compatible"
    except Exception as e:
        return f"Error al obtener la dirección IP: {str(e)}"

local_ip = get_local_ip()
print(f"Tu dirección IP local es: {local_ip}")
