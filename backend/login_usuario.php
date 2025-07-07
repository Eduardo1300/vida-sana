<?php
header("Content-Type: application/json; charset=UTF-8");
ini_set('display_errors', 1);
error_reporting(E_ALL);

$host = "127.0.0.1";
$user = "root";
$pass = "";
$db = "vida_sana";
$port = 3307;

$conn = new mysqli($host, $user, $pass, $db, $port);
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Error de conexión"]);
    exit;
}

$raw = file_get_contents("php://input");
$data = json_decode($raw, true);

if (!$data || !isset($data["usuario"]) || !isset($data["clave"])) {
    echo json_encode(["success" => false, "message" => "Datos incompletos"]);
    exit;
}

$usuario = $data["usuario"];
$clave = $data["clave"];

$stmt = $conn->prepare("SELECT id, clave FROM usuarios WHERE nombre_usuario = ?");
$stmt->bind_param("s", $usuario);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["success" => false, "message" => "Usuario no encontrado"]);
    exit;
}

$fila = $result->fetch_assoc();

if (password_verify($clave, $fila["clave"])) {
    echo json_encode(["success" => true, "id" => $fila["id"]]);
} else {
    echo json_encode(["success" => false, "message" => "Clave incorrecta"]);
}
?>
