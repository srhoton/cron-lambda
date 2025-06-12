resource "null_resource" "gradle_build" {
  triggers = {
    source_hash = filemd5("${path.module}/../lambda/src/main/java/com/steverhoton/poc/EventBridgeLambdaHandler.java")
    build_hash  = filemd5("${path.module}/../lambda/build.gradle")
  }

  provisioner "local-exec" {
    command     = "./gradlew build"
    working_dir = "${path.module}/../lambda"
  }
}

resource "aws_lambda_function" "cron_lambda" {
  filename         = "${path.module}/../lambda/build/libs/cronlambda-1.0.0.jar"
  function_name    = "${var.project_name}-${var.environment}"
  role             = aws_iam_role.lambda_execution_role.arn
  handler          = "com.steverhoton.poc.EventBridgeLambdaHandler::handleRequest"
  runtime          = "java17"
  memory_size      = var.lambda_memory_size
  timeout          = var.lambda_timeout
  source_code_hash = filebase64sha256("${path.module}/../lambda/build/libs/cronlambda-1.0.0.jar")

  environment {
    variables = {
      ENVIRONMENT = var.environment
    }
  }

  depends_on = [
    aws_iam_role_policy_attachment.lambda_logs,
    aws_cloudwatch_log_group.lambda_logs,
    null_resource.gradle_build,
  ]
}

resource "aws_cloudwatch_log_group" "lambda_logs" {
  name              = "/aws/lambda/${var.project_name}-${var.environment}"
  retention_in_days = 14
}