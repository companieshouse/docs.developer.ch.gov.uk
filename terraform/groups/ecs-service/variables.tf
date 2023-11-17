# ------------------------------------------------------------------------------
# Environment
# ------------------------------------------------------------------------------
variable "environment" {
  type        = string
  description = "The environment name, defined in envrionments vars."
}
variable "aws_region" {
  default     = "eu-west-2"
  type        = string
  description = "The AWS region for deployment."
}
variable "aws_profile" {
  default     = "development-eu-west-2"
  type        = string
  description = "The AWS profile to use for deployment."
}

# ------------------------------------------------------------------------------
# Docker Container
# ------------------------------------------------------------------------------
variable "docker_registry" {
  type        = string
  description = "The FQDN of the Docker registry."
}

# ------------------------------------------------------------------------------
# Service performance and scaling configs
# ------------------------------------------------------------------------------
variable "desired_task_count" {
  type = number
  description = "The desired ECS task count for this service"
  default = 1 # defaulted low for dev environments, override for production
}
variable "max_task_count" {
  type        = number
  description = "The maximum number of tasks for this service."
  default     = 3
}
variable "required_cpus" {
  type = number
  description = "The required cpu resource for this service. 1024 here is 1 vCPU"
  default = 128 # defaulted low for dev environments, override for production
}
variable "required_memory" {
  type = number
  description = "The required memory for this service"
  default = 256 # defaulted low for java service in dev environments, override for production
}

variable "use_fargate" {
  type        = bool
  description = "If true, sets the required capabilities for all containers in the task definition to use FARGATE, false uses EC2"
  default     = true
}

variable "use_capacity_provider" {
  type        = bool
  description = "Whether to use a capacity provider instead of setting a launch type for the service"
  default     = true
}
variable "service_autoscale_enabled" {
  type        = bool
  description = "Whether to enable service autoscaling, including scheduled autoscaling"
  default     = true
}
variable "service_autoscale_target_value_cpu" {
  type        = number
  description = "Target CPU percentage for the ECS Service to autoscale on"
  default     = 50 # 100 disables autoscaling using CPU as a metric
}
variable "service_scaledown_schedule" {
  type        = string
  description = "The schedule to use when scaling down the number of tasks to zero."
  default     = ""
}
variable "service_scaleup_schedule" {
  type        = string
  description = "The schedule to use when scaling up the number of tasks to their normal desired level."
  default     = ""
}

# ----------------------------------------------------------------------
# Cloudwatch alerts
# ----------------------------------------------------------------------
variable "cloudwatch_alarms_enabled" {
  description = "Whether to create a standard set of cloudwatch alarms for the service.  Requires an SNS topic to have already been created for the stack."
  type        = bool
  default     = true
}

# ------------------------------------------------------------------------------
# Service environment variable configs
# ------------------------------------------------------------------------------
variable "log_level" {
  default     = "info"
  type        = string
  description = "The log level for services to use: trace, debug, info or error"
}

variable "use_set_environment_files" {
  type        = bool
  default     = false
  description = "Toggle default global and shared  environment files"
}

variable "docs_developer_version" {
  type        = string
  description = "The version of the docs.developer.ch.gov.uk container to run."
}

variable "cdn_host" {
  type        = string
}
variable "chs_url" {
  type        = string
}
variable "account_local_url" {
  type        = string
}
variable "dev_specs_url" {
  type        = string
}
variable "piwik_url" {
  type        = string
}
variable "piwik_site_id" {
  type        = string
}
variable "redirect_uri" {
  type        = string
  default     = "/"
}
variable "cache_pool_size" {
  type        = string
  default     = "8"
}
variable "cache_server" {
  type        = string
}
variable "cookie_domain" {
  type        = string
}
variable "cookie_name" {
  type        = string
  default     = "__SID"
}
variable "cookie_secure_only" {
  type        = string
  default     = "0"
}
variable "default_session_expiration" {
  type        = string
  default     = "3600"
}
variable "oauth2_redirect_uri" {
  type        = string
}
variable "oauth2_auth_uri" {
  type        = string
}
