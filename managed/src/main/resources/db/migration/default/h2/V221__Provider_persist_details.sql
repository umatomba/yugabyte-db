ALTER TABLE IF EXISTS provider ADD COLUMN IF NOT EXISTS details json_alias;
ALTER TABLE provider ALTER COLUMN details TYPE binary varying;