const body = document.querySelector('#sources');
const notice = document.querySelector('#notice');
async function loadSources() {
  try {
    const response = await fetch('/api/admin/data-sources');
    if (!response.ok) throw new Error(`Could not load sources (${response.status})`);
    const data = await response.json();
    body.replaceChildren();
    for (const source of data.sources) {
      const row = document.createElement('tr');
      for (const value of [source.id, source.status, source.lastRun || 'Never', source.lastError || '—']) {
        const cell = document.createElement('td'); cell.textContent = value; row.append(cell);
      }
      const action = document.createElement('td'); const button = document.createElement('button');
      button.type = 'button'; button.textContent = 'Run now'; button.disabled = !source.enabled;
      button.setAttribute('aria-label', `Run ${source.id} now`);
      button.addEventListener('click', async () => {
        button.disabled = true; notice.textContent = `Refreshing ${source.id}…`;
        try { const result = await fetch(`/api/admin/data-sources/${encodeURIComponent(source.id)}/run`, {method:'POST'}); if (!result.ok) throw new Error(`Refresh failed (${result.status})`); notice.textContent = `${source.id} refresh completed.`; }
        catch (error) { notice.textContent = error.message; }
        finally { await loadSources(); }
      });
      action.append(button); row.append(action); body.append(row);
    }
  } catch (error) { notice.textContent = error.message; }
}
async function loadSample() { try { const r = await fetch('/api/sample/companies'); const d = await r.json(); document.querySelector('#sample').textContent = `${d.label}\n${JSON.stringify(d.companies,null,2)}`; } catch { document.querySelector('#sample').textContent = 'Sample data unavailable.'; } }
loadSources(); loadSample();
