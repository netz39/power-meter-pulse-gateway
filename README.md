# Power Meter Pulse Gateway

> Gateway for Power Meter Pulses from ESP32 to RabbitmQ

## Configuration

Configuration is done using environment variables:

* `PORT`: Port for the HTTP endpoint (default `8080`, only change when running locally!)
* `AMQP_HOST`: RabbitMQ host
* `AMQP_USER`: RabbitMQ user
* `AMQP_PASS`: RabbitMQ password
* `AMQP_VHOST`: RabbitMQ virtual host, defaults to '/'
* `API_TOKEN`: An API token for accessing the endpoint (default: empty, no authorization)

## API

### Pulse Message

Pulses are encoded as messages with a [UNIX timestamp](https://www.unixtimestamp.com/) in the JSON form
```json
{
  "timestamp": 1234567890
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

Please note that the gateway send any valid JSON message to the AMQP queue. 
Idempotency is not considered by the gateway in the sense that an already sent timestamp will not be suppressed.

**Note: In the current implementation only a log message is posted.**


## Deployment

### Development

This project uses the [Micronaut Framework](https://micronaut.io/).

Version numbers are determined with [jgitver](https://jgitver.github.io/).
Please check your [IDE settings](https://jgitver.github.io/#_ides_usage) to avoid problems, as there are still some unresolved issues.
If you encounter a project version `0` there is an issue with the jgitver generator.

For local execution the configuration can be provided in a `.env` file and made available using `dotenv`:
```bash
dotenv ./mvnw mn:run
```

Note that `.env` is part of the `.gitignore` and can be safely stored in the local working copy.

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
