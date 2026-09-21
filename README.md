# Professores vs Zumbiversitários 🧟‍♂️☕

Mini-jogo em Java, inspirado em *Plants vs Zombies*, desenvolvido como TP da disciplina de CDC. O objetivo é aplicar conceitos de POO (herança, polimorfismo, interfaces) e trabalhar em equipe com um projeto Java completo, incluindo interface gráfica.

## Tema

Os **professores do curso** fazem o papel das plantas e atiram **linguagens de programação** nos zumbis (alunos zumbis, sobrecarregados de provas, trabalhos, prazos e TCC). O chão é substituído por **tomadas**, e os girassóis (fonte de recurso) viram **cafeteiras**, já que o dinheiro do jogo é **café**.

## Telas do jogo

| # | Tela | Descrição |
|---|------|-----------|
| 1 | Menu Principal | Jogar / Extra / Créditos / Sair |
| 2 | Extra | Configurações e opções adicionais |
| 3 | Créditos | Equipe e agradecimentos |
| 4 | Seleção de Nível | Escolha da fase |
| 5 | Seleção de Professores | Escolha do "loadout" de professores antes da fase |
| 6 | Fase (Gameplay) | Jogo em si — 5 fileiras, zumbis avançando |
| 7 | Fim de Fase *(opcional)* | Tela ou overlay de vitória/derrota |

## Arquitetura de classes (visão geral)

### Modelo (lógica do jogo)

```
Entidade (abstrata)
 ├── Professor (abstrata)         → vida, linha, coluna, custoCafe, cadenciaTiro
 │     ├── ProfessorPhilipe       → atira TiroPython
 │     ├── ProfessoraThais        → atira ...
 │     ├── ProfessorMarcus        → atira ...
 │     ├── ProfessorDaniel        → atira C
 │     ├── ProfessoraGlaucia      → atira ...
 │     ├── ProfessorNacif         → atira Verilog
 │     ├── ProfessorFabricio      → atira Scalla
 │     ├── ProfessorLucas         → atira ...
 │     ├── ...                    → (até 8 professores)
 │     └── Cafeteira              → produz café periodicamente (não atira)
 └── Zumbi (abstrata)             → vida, velocidade, dano, linha
       ├── ZumbiVibeCoder         → fraco, rápido, atrapalhado
       ├── ZumbiCalouro           → comum
       ├── ZumbiCalouroprotegido  → comum porém cone
       ├── ZumbiCalouroblindado   → comum porém balde de ferro
       ├── ZumbiViciado           → comum porém fica rápido, perigoso e extressado ao quebrar seu telefone
       ├── ZumbiTrote             → rápido, pouca vida
       ├── ZumbiFormando          → minitanque (bastante vida, lento)
       └── ZumbiDoutorando        → tanque (extrema vida, extremamente lento)
```

- **Tiro / Projetil** — classe (ou hierarquia) responsável pelo disparo. Atributos configuráveis: velocidade, dano, sprite, efeito visual e som — pensados para serem fáceis de ajustar sem mexer na lógica de movimento (via config/enum).
- **Tomada** — célula do tabuleiro (linha, coluna, professor instalado).
- **Tabuleiro** — matriz 5×N de `Tomada`.
- **Onda** — controla o spawn de zumbis ao longo do tempo.
- **Fase** — configuração de uma fase (ondas, professores disponíveis, café inicial).
- **GerenciadorDeJogo** — motor do jogo: loop principal, colisões, geração de recurso, condição de vitória/derrota.

### Interfaces

- `Atacavel` — quem pode atirar.
- `Danificavel` — quem recebe dano e possui vida.
- `ProdutorDeRecurso` — quem gera café (implementada por `Cafeteira`).

### Interface gráfica (telas)

Cada tela é um par **FXML + Controller**, trocadas por um `GerenciadorDeTelas` (responsável por alternar a `Scene` ativa no `Stage`).

## Tecnologia

- **Java + JavaFX** (interface gráfica via Scene Builder / FXML)
- `AnimationTimer` para o loop de movimento/colisão dos projéteis e zumbis
- `AudioClip` para efeitos sonoros (tiros) e `MediaPlayer` para música de fundo
- Configuração de atributos de tiro (cadência, velocidade, som, efeito) centralizada, para facilitar ajustes de balanceamento entre a equipe

## Mecânica básica

1. Jogador escolhe a fase e monta seu time de professores (limitado pelo café disponível).
2. Ondas de zumbis avançam pelas 5 fileiras.
3. Professores posicionados nas tomadas atiram automaticamente conforme sua cadência.
4. Cafeteiras geram café periodicamente, usado para posicionar novos professores.
5. Fase termina em vitória (todas as ondas sobrevividas) ou derrota (zumbi atravessa o tabuleiro).

## Equipe

Gean Augusto Lino de Oliveira - 6578
Luiz Henrique Laudares - 6596
Thomaz Augusto Araújo Silva - 6577
Jordane Andrade Soares - 5106
Rafael de Matos Silva - 6575
Mateus Henrique Braz - 6594
Passur Caio Lélis Alves - 6590

## Status do projeto

🚧 Em desenvolvimento — TP da disciplina de CDC.
