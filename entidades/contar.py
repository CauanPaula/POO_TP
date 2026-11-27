
from PIL import Image
import os

# 1. Abre a spritesheet
img = Image.open("./abelha_menu.png").convert("RGBA")
largura, altura = img.size

# 2. Remove o fundo quadriculado
pixels = img.load()

for y in range(altura):
    for x in range(largura):

        r, g, b, a = pixels[x, y]

        # Verifica se o pixel é praticamente cinza
        diferenca = max(r, g, b) - min(r, g, b)
        brilho = (r + g + b) // 3

        # Pixels neutros claros/cinzas são considerados fundo
        if diferenca < 30 and brilho > 110:
            pixels[x, y] = (0, 0, 0, 0)

# 3. Configura a grade
colunas = 4
linhas = 4

w = largura // colunas
h = altura // linhas

# 4. Separa os 16 frames
frames = []

for linha in range(linhas):
    for coluna in range(colunas):

        caixa = (
            coluna * w,
            linha * h,
            (coluna + 1) * w,
            (linha + 1) * h
        )

        frame = img.crop(caixa)

        # Identifica a área visível
        bbox = frame.getbbox()

        if bbox:
            frame = frame.crop(bbox)
            frames.append(frame)

# 5. Define o tamanho final
canvas_size = 128
margem = 8
area_maxima = canvas_size - margem * 2

# Descobre o maior sprite
maior_largura = max(f.width for f in frames)
maior_altura = max(f.height for f in frames)

# Mantém proporção e escala uniforme
escala = min(
    area_maxima / maior_largura,
    area_maxima / maior_altura
)

# 6. Salva os frames
os.makedirs("imagens/menu/besouro/menu", exist_ok=True)

contador = 0

for frame in frames:

    novo_w = int(frame.width * escala)
    novo_h = int(frame.height * escala)

    frame = frame.resize(
        (novo_w, novo_h),
        Image.Resampling.LANCZOS
    )

    # Canvas transparente
    canvas = Image.new(
        "RGBA",
        (canvas_size, canvas_size),
        (0, 0, 0, 0)
    )

    # Centraliza
    pos_x = (canvas_size - novo_w) // 2
    pos_y = (canvas_size - novo_h) // 2

    canvas.alpha_composite(frame, (pos_x, pos_y))

    canvas.save(
        f"imagens/menu/besouro/frame_{contador:02d}.png"
    )

    contador += 1

print("16 frames salvos em imagens/menu/besouro/")
