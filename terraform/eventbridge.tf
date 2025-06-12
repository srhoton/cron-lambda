resource "aws_cloudwatch_event_rule" "cron_schedule" {
  name                = "${var.project_name}-schedule-${var.environment}"
  description         = "Trigger for ${var.project_name} Lambda function"
  schedule_expression = var.schedule_expression
}

resource "aws_cloudwatch_event_target" "lambda_target" {
  rule      = aws_cloudwatch_event_rule.cron_schedule.name
  target_id = "LambdaTarget"
  arn       = aws_lambda_function.cron_lambda.arn
}

resource "aws_lambda_permission" "allow_eventbridge" {
  statement_id  = "AllowExecutionFromEventBridge"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.cron_lambda.function_name
  principal     = "events.amazonaws.com"
  source_arn    = aws_cloudwatch_event_rule.cron_schedule.arn
}