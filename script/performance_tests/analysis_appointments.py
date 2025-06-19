# Dati raccolti per i 10 pazienti

print("Analisi delle performance con e senza indice su 'patient._id' per 10 pazienti con più visite totali")

with_index = {
    "execution_time": [1710, 86, 467, 228, 54, 330, 262, 37, 25, 133],
    "docs_examined":  [23234, 3408, 2655, 2558, 2580, 2562, 2544, 2329, 2281, 2195],
    "index_examined": [23234, 3408, 2655, 2558, 2580, 2562, 2544, 2329, 2281, 2195],
    "docs_returned":  [23234, 3408, 2655, 2558, 2580, 2562, 2544, 2329, 2281, 2195]
}

without_index = {
    "execution_time": [1379, 522, 1026, 949, 619, 1023, 1191, 806, 531, 882],
    "docs_examined":  [699989]*10,
    "index_examined": [0]*10,
    "docs_returned":  [23234, 3408, 2655, 2558, 2580, 2562, 2544, 2329, 2281, 2195]
}

def average(lst):
    return sum(lst) / len(lst)

# Calcoli medie con indice
avg_with_index = {k: average(v) for k, v in with_index.items()}
# Calcoli medie senza indice
avg_without_index = {k: average(v) for k, v in without_index.items()}

print("Average metrics WITH index:")
for k, v in avg_with_index.items():
    print(f"  {k}: {v:.2f}")

print("\nAverage metrics WITHOUT index:")
for k, v in avg_without_index.items():
    print(f"  {k}: {v:.2f}")

print("\nPercent change (WITH vs WITHOUT index):")
for k in avg_with_index:
    with_val = avg_with_index[k]
    without_val = avg_without_index[k]
    if without_val != 0:
        percent_change = ((with_val - without_val) / without_val) * 100
        print(f"  {k}: {percent_change:+.2f}%")
    else:
        print(f"  {k}: N/A (division by zero)")
