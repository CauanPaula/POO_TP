from PIL import Image
import os

# 1. Abre a imagem e converte para RGBA (com canal de transparência)
img = Image.open("spritesheet.webp").convert("RGBA")
largura, altura = img.size

# 2. Remove o fundo preto (transforma pixels muito escuros em transparentes)
pixels = img.getdata()
novos_pixels = []
for r, g, b, a in pixels:
    if r < 20 and g < 20 and b < 20:  # Se for preto/quase preto
        novos_pixels.append((0, 0, 0, 0))  # Transparente
    else:
        novos_pixels.append((r, g, b, a))
img.putdata(novos_pixels)

# 3. Define a grade (6 colunas x 3 linhas)
colunas = 6
linhas = 3
w = largura // colunas
h = altura // linhas

os.makedirs("imagens/besouro", exist_ok=True)

# 4. Corta cada quadro e salva numerado
contador = 1
for linha in range(linhas):
    for coluna in range(colunas):
        caixa = (coluna * w, linha * h, (coluna + 1) * w, (linha + 1) * h)
        frame = img.crop(caixa)
        frame.save(f"imagens/besouro/frame_{contador:02d}.png", "PNG")
        contador += 1

print("Pronto! 18 sprites salvos na pasta imagens/besouro/")