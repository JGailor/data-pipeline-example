# Overview
This project is an example of a possible data-pipeline architecture that you can build and run locally, and then release without any changes to code / structure by leveraging the provisioning handled by the ```provisioning``` project which uses Ansible and Vagrant to deploy to staging, production, or any other environment you want to process data.

## Components
1. ```emitter```: This is the source of data.  It generates a series of messages, emits them to a RabbitMQ queue, and then sleeps for a bit before closing the queue and deleting it.

2. ```time-tracker```: Reads messages off the RabbitMQ queue, decodes the json, adds a timestamp to the message, and then re-emits it to an outbound queue.

3. ```persister```: Reads the messages off the outbound queue from ```time-tracker``` and saves the message to a Postgresql database.

4. ```provisioning```: Builds and runs a multi-VM environment using Vagrant, and configures the VMs using Ansible.  One of the powerful implications of this setup is that simply by creating an Ansible inventory file for a new environment (staging, production, etc.) you can reuse the same Ansible playbooks for deploying to any environment.

## Notes
- My initial benchmarks on my Macbook Pro, before persisting the data to Postgres, showed a throughput of ~400k messages/minute.  Once I added the Postgres persistence as a synchronous operation, performance fell to ~50k messages/minute.  The next iteration will involve splitting out Postgres persistence, adding Elasticsearch indexing as it's own process, and then using RabbitMQ exchanges to fan-out the messages to each process and measuring the performance.
