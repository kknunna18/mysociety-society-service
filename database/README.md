# Society Service database scripts

`migrations/V1__society_schema.sql` is the versioned PostgreSQL 16+ schema
script for the Society Service. It creates the `pgcrypto` extension, the
`mysociety` schema, and only the Society-owned tables:

- `societies`
- `buildings`
- `units`
- `household_memberships`
- `vehicles`
- `society_settings`

No Flyway or other migration runner is configured. Apply the scripts in
version order with a PostgreSQL role permitted to create the schema and the
`pgcrypto` extension:

```sh
psql --set ON_ERROR_STOP=1 --dbname=mysociety --file=database/migrations/V1__society_schema.sql
```

The setup statements and all table/index creation statements are idempotent.
The script is intentionally not a schema-upgrade tool: after it has been
applied, add a new higher-versioned script for every compatible schema change
rather than editing `V1__society_schema.sql`.

## Ownership and external references

The Society Service owns all six tables above and the foreign keys among them:
`buildings.society_id`, `units.society_id`, `units.building_id`,
`household_memberships.society_id`, `household_memberships.unit_id`,
`vehicles.society_id`, `vehicles.unit_id`, and `society_settings.society_id`.

`household_memberships.user_id`, `vehicles.owner_user_id`, and
`society_settings.created_by` are Identity Service user UUIDs. They
deliberately have no database foreign keys because `app_users` belongs to the
Identity Service and is not part of this service's schema ownership boundary.
The Society API must validate caller/user UUIDs from authenticated Identity
JWT claims or Identity Service API responses before creating or changing these
references. Identity user-deletion or deactivation events must be consumed to
apply the Society Service's business policy for affected memberships, vehicles,
and audit references; PostgreSQL cascading behavior is not available across
the service boundary.

Hibernate remains configured with `ddl-auto=validate`; the application
validates this pre-provisioned schema and does not create or migrate it.
