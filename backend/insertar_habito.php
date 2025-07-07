<?php
header("Content-Type: application/json; charset=UTF-8");

// Configuración de conexión
$host = "127.0.0.1";
$db = "vida_sana";
$user = "root";
$pass = "";
$port = 3307;


// Conexión a la base de datos
$conn = new mysqli($host, $user, $pass, $db, $port);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Conexión fallida: " . $conn->connect_error]);
    exit;
}

// Obtener el JSON bruto
$raw = file_get_contents("php://input");
file_put_contents("log.txt", "RAW: " . $raw . PHP_EOL, FILE_APPEND); // 🟨 Guarda en log.txt

// Convertir el JSON a array asociativo
$data = json_decode($raw, true);

// Validar datos recibidos
if (!$data || !isset($data["tipo"]) || !isset($data["cantidad"]) || !isset($data["fecha"])) {
    echo json_encode(["success" => false, "message" => "JSON inválido o faltan campos"]);
    exit;
}

// Extraer datos
$tipo = $data["tipo"];
$cantidad = $data["cantidad"];
$fecha = $data["fecha"];
$usuario_id = $data["usuario_id"];


// Insertar en base de datos
$stmt = $conn->prepare("INSERT INTO habitos (tipo, cantidad, fecha, usuario_id) VALUES (?, ?, ?, ?)");
$stmt->bind_param("sssi", $tipo, $cantidad, $fecha, $usuario_id);

$success = $stmt->execute();

if ($success) {
    echo json_encode(["success" => true, "message" => "Insertado correctamente"]);
} else {
    echo json_encode(["success" => false, "message" => "Error al insertar en la base de datos"]);
}

// Cerrar conexión
$conn->close();
?>
