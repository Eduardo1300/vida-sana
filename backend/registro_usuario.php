<?php
header("Content-Type: application/json; charset=UTF-8");

// Configuración
$host = "127.0.0.1";
$user = "root";
$pass = "";
$db = "vida_sana";
$port = 3307; // o 3307 según tu configuración

// Conexión
$conn = new mysqli($host, $user, $pass, $db, $port);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Error de conexión"]);
    exit;
}

// Obtener JSON
$raw = file_get_contents("php://input");
$data = json_decode($raw, true);

// Validar
if (!$data || !isset($data["usuario"]) || !isset($data["clave"])) {
    echo json_encode(["success" => false, "message" => "Datos incompletos"]);
    exit;
}

$usuario = $data["usuario"];
$clave = password_hash($data["clave"], PASSWORD_DEFAULT); // Seguridad

// Verificar si existe
$check = $conn->prepare("SELECT id FROM usuarios WHERE nombre_usuario = ?");
$check->bind_param("s", $usuario);
$check->execute();
$check->store_result();
if ($check->num_rows > 0) {
    echo json_encode(["success" => false, "message" => "Usuario ya existe"]);
    exit;
}

// Insertar
$stmt = $conn->prepare("INSERT INTO usuarios (nombre_usuario, clave) VALUES (?, ?)");
$stmt->bind_param("ss", $usuario, $clave);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Usuario registrado"]);
} else {
    echo json_encode(["success" => false, "message" => "Error al registrar"]);
}

$conn->close();
?>
