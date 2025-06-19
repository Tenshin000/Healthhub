print("Average Metrics Analysis with and without Index on 'doctor._id' for Earnings Analytics")

with_index = {
    "execution_time": [12, 18, 13, 14, 13, 9, 14, 17, 15, 12],
    "docs_examined":  [1671, 1534, 1210, 938, 934, 911, 905, 887, 885, 885],
    "index_examined": [1671, 1534, 1210, 938, 934, 911, 905, 887, 885, 885],
    "docs_returned":  [6, 6, 6, 5, 6, 6, 6, 6, 6, 6]
}

without_index = {
    "execution_time": [1067, 521, 474, 603, 442, 555, 467, 449, 477, 613],
    "docs_examined":  [699989]*10,
    "index_examined": [0]*10,
    "docs_returned":  [6, 6, 6, 5, 6, 6, 6, 6, 6, 6]
}

def average(lst):
    return sum(lst) / len(lst)

avg_with_index = {k: average(v) for k, v in with_index.items()}
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
