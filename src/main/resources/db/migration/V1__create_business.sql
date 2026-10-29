-- Negócio atendido pelo sistema. O campo "segment" aponta para o pacote de
-- segmento (barbearia, mecanica, petshop...) que define labels, fluxo e campos.
-- "settings" guarda personalizações livres do negócio (tema, horário etc.).
CREATE TABLE business (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(120) NOT NULL,
    segment     VARCHAR(40)  NOT NULL,
    settings    JSONB        NOT NULL DEFAULT '{}'::jsonb,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
