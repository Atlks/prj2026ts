import {
  css,
  html,
  LitElement,
} from 'https://cdn.skypack.dev/lit';

class UserTable extends LitElement {
  static styles = css`
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
    }
    th, td {
      padding: 8px;
      border: 1px solid #ddd;
      text-align: left;
    }
    th {
      background-color: #f2f2f2;
    }
  `;

  static properties = {
    users: { type: Array }
  };

  constructor() {
    super();
    // 初始化用户列表
    this.users = [
      { id: 1, name: 'John Doe11', email: 'john.doe@example.com' },
      { id: 2, name: 'Jane Smit222h', email: 'jane.smith@example.com' },
      { id: 3, name: 'Alice Johnson', email: 'alice.johnson@example.com' },
    ];
  }

  render() {
    return html`
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
          </tr>
        </thead>
        <tbody>
          ${this.users.map(user => html`
            <tr>
              <td>${user.id}</td>
              <td>${user.name}</td>
              <td>${user.email}</td>
            </tr>
          `)}
        </tbody>
      </table>
    `;
  }
}

customElements.define('user-table', UserTable);

