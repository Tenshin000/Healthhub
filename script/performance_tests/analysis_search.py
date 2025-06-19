# Ricalcolo dopo il reset dell'ambiente

# Dati estratti dalle analisi per ricerca "regex" e "text search"
regex_data = {
    "execution_time": [344, 215, 207, 254, 477, 213, 209],
    "docs_examined": [87632] * 7,
    "index_keys_examined": [0] * 7,
    "docs_returned": [1738, 0, 1, 6298, 58962, 115, 0]
}

text_data = {
    "execution_time": [25, 74, 2, 37, 458, 3, 159],
    "docs_examined": [1738, 9648, 58, 5325, 60346, 115, 21337],
    "index_keys_examined": [1738, 9652, 59, 5325, 178271, 115, 22356],
    "docs_returned": [1738, 9648, 58, 5325, 60346, 115, 21337]
}

def average(lst):
    return sum(lst) / len(lst)

avg_regex = {k: average(v) for k, v in regex_data.items()}
avg_text = {k: average(v) for k, v in text_data.items()}

print("Average Metrics Analysis with and without Index for 'regex' and 'text search' queries")
print("Average metrics WITH index:")
for k, v in avg_text.items():
    print(f"  {k}: {v:.2f}")

print("\nAverage metrics WITHOUT index:")
for k, v in avg_regex.items():
    print(f"  {k}: {v:.2f}")

print("\nPercent change (WITH vs WITHOUT index):")
for k in avg_text:
    with_val = avg_text[k]
    without_val = avg_regex[k]
    if without_val != 0:
        percent_change = ((with_val - without_val) / without_val) * 100
        print(f"  {k}: {percent_change:+.2f}%")
    else:
        print(f"  {k}: N/A (division by zero)")