/** 将分类树扁平化为下拉选项 */
export function flattenCategories(nodes, prefix = '') {
  const result = []
  for (const node of nodes || []) {
    const label = prefix ? `${prefix} / ${node.name}` : node.name
    result.push({ id: node.id, label })
    if (node.children?.length) {
      result.push(...flattenCategories(node.children, label))
    }
  }
  return result
}
