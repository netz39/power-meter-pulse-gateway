# Power Meter Pulse Gateway

> Gateway for Power Meter Pulses from an ESP32 (or another REST client) to RabbitMQ

## Configuration

The service is configure with environment variables:

* `AMQP_HOST`: RabbitMQ host
* `AMQP_USER`: RabbitMQ user
* `AMQP_PASS`: RabbitMQ password
* `AMQP_VHOST`: RabbitMQ virtual host, defaults to '/'
* `PULSE_BINDING`: RabbitMQ routing key name for pulse messages
* `API_TOKEN`: An API token for accessing the endpoint (default: empty, no authorization)
* `PORT`: Port for the HTTP endpoint (default `8080`, only change when running locally!)

## API

### Pulse Message

Pulses are encoded as messages with an [ISO 8601 timestamp](https://en.wikipedia.org/wiki/ISO_8601) in the JSON form
```json
{
  "timestamp": "2022-08-23T16:54:23Z"
}
```

### REST

Post a pulse message by calling `POST /pulse` with the above pulse message JSON form.

If authorization is configured, the `API_TOKEN` must be provided as bearer token in the `Authorization` header, e.g.
```
Authorization: Bearer hu3aiF9enaXa5BaesieNgafe5tee0The
```

This call returns one of the following codes:
* `201` if the call was successful (created)
* `400` if the body argument is invalid
* `401` if authorization failed
* `504` if the AMQP queue is not available.

On return code `504` the call should be retried at a later point. In any other case the call should not be retried.

Please note that idempotency is not considered by the gateway in the sense that an already sent timestamp will not be suppressed.

An [OAS3 schema ](https://swagger.io/specification/) can be obtained under the  
URL `/swagger/power-meter-pulse-gateway-0.1.yml`.

### AMQP / RabbitMQ

A pulse message that is received on the REST endpoint is sent to an AMQP queue with the routing key `PULSE_BINDING` on 
the default exchange.

Note that there is a deserialization/serialization step in this process, so invalid JSON or extra properties will be 
caught by the gateway and not emitted to the exchange.


## Deployment

Both deployment methods below expect the configuration in a `.env` file.
This file is part of the `.gitignore` and can safely be left in the local working copy.

### Docker

The gateway is intended to be run as a Docker container:
```bash
docker run --rm \ 
  -p 8080:8080 \
  --env-file .env \
  netz39/power-meter-pulse-gateway
```

### Development

This project uses the [Micronaut Framework](https://micronaut.io/).

Version numbers are determined with [jgitver](https://jgitver.github.io/).
Please check your [IDE settings](https://jgitver.github.io/#_ides_usage) to avoid problems, as there are still some unresolved issues.
If you encounter a project version `0` there is an issue with the jgitver generator.

To use the configuration from a `.env` file, run using [dotenv](https://github.com/therootcompany/dotenv):
```bash
dotenv ./mvnw mn:run
```

## Build

The build is split into two stages:
1. Packaging with [Maven](https://maven.apache.org/)
2. Building the Docker container

This means that the [Dockerfile](Dockerfile) expects one (and only one) JAR file in the target directory.
Build as follows:

```bash
mvn --batch-mode --update-snapshots clean package
docker build .
```

The process is coded in the [docker-publish workflow](.github/workflows/docker-publish.yml) and only needs to be
executed manually for local builds.


## Maintainers

* Stefan Haun ([@penguineer](https://github.com/penguineer))


## Contributing

PRs are welcome!

If possible, please stick to the following guidelines:

* Keep PRs reasonably small and their scope limited to a feature or module within the code.
* If a large change is planned, it is best to open a feature request issue first, then link subsequent PRs to this issue, so that the PRs move the code towards the intended feature.


## License

[MIT](LICENSE.txt) © 2022 Netz39 e.V. and contributors
