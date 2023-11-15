data "aws_vpc" "vpc" {
  filter {
    name   = "tag:Name"
    values = [local.vpc_name]
  }
}

data "aws_ecs_cluster" "ecs_cluster" {
  cluster_name = "${local.name_prefix}-cluster"
}
data "aws_iam_role" "ecs_cluster_iam_role" {
  name = "${local.name_prefix}-ecs-task-execution-role"
}

data "aws_lb" "dev_site_lb" {
  name = "dev-site-${var.environment}-lb"
}
data "aws_lb_listener" "dev_site_lb_listener" {
  load_balancer_arn = data.aws_lb.dev_site_lb.arn
  port              = 443
}

# retrieve all secrets for this stack using the stack path
data "aws_ssm_parameters_by_path" "secrets" {
  path = "/${local.name_prefix}"
}
# create a list of secrets names to retrieve them in a nicer format and lookup each secret by name
data "aws_ssm_parameter" "secret" {
  for_each = toset(data.aws_ssm_parameters_by_path.secrets.names)
  name     = each.key
}

# retrieve all global secrets for this env using global path
data "aws_ssm_parameters_by_path" "global_secrets" {
  path = "/${local.global_prefix}"
}
# create a list of secrets names to retrieve them in a nicer format and lookup each secret by name
data "aws_ssm_parameter" "global_secret" {
  for_each = toset(data.aws_ssm_parameters_by_path.global_secrets.names)
  name     = each.key
}

// --- s3 bucket for shared services config ---
data "vault_generic_secret" "shared_s3" {
  path = "aws-accounts/shared-services/s3"
}